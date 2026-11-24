package com.antony.nextrade.config.upload;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class R2FileUploadServiceTest {
    private final S3Client client = mock(S3Client.class);
    private final R2FileUploadService service = new R2FileUploadService(client,
            new CloudflareR2Properties(true, "account", "test-key", "test-secret",
                    "photos", "https://photos.example.test/"));
    private final byte[] photo = {1, 2, 3};
    private final MockMultipartFile file = new MockMultipartFile("avatar", "original.html", "image/png", photo);

    @Test
    void savesPhotoWithGeneratedKeyAndReturnsPublicUrl() throws IOException {
        var url = service.upload(file, "avatars/user-id");

        var object = ArgumentCaptor.forClass(PutObjectRequest.class);
        var body = ArgumentCaptor.forClass(RequestBody.class);
        verify(client).putObject(object.capture(), body.capture());
        var request = object.getValue();
        assertThat(request.bucket()).isEqualTo("photos");
        assertThat(request.key()).matches("nextrade/avatars/user-id/[0-9a-f-]{36}\\.png");
        assertThat(request.contentType()).isEqualTo("image/png");
        assertThat(request.contentLength()).isEqualTo(3L);
        try (var stream = body.getValue().contentStreamProvider().newStream()) {
            assertThat(stream.readAllBytes()).isEqualTo(photo);
        }
        assertThat(url).isEqualTo("https://photos.example.test/" + request.key());
    }

    @Test
    void reportsAnUpstreamStorageErrorAsBadGateway() {
        when(client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder().statusCode(403).build());

        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> service.upload(file, "avatars/user-id"))
                .satisfies(exception -> assertThat(exception.getStatusCode().value()).isEqualTo(502));
    }
}
