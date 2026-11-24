package com.antony.nextrade.modules.users.usecase.updateprofile;

import com.antony.nextrade.config.upload.IFileUploadService;
import com.antony.nextrade.modules.users.repository.IUserRepository;
import com.antony.nextrade.modules.users.usecase.findprofile.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateProfilePhotosUseCase {
    private static final int MAX_PHOTO_BYTES = 5 * 1024 * 1024;
    private static final int MAX_PHOTO_DIMENSION = 8000;
    private static final Set<String> PHOTO_TYPES = Set.of("image/jpeg", "image/png", "image/gif");

    private final IUserRepository userRepository;
    private final IFileUploadService fileUploadService;

    @Transactional
    public ProfileResponse execute(UUID userId, UpdateProfilePhotosRequest request) {
        var user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        boolean hasAvatar = hasFile(request.avatar());
        boolean hasCover = hasFile(request.cover());
        if (hasAvatar) {
            validatePhoto(request.avatar());
        }
        if (hasCover) {
            validatePhoto(request.cover());
        }

        if (hasAvatar) {
            user.setAvatarUrl(fileUploadService.upload(request.avatar(), "avatars/" + userId));
        } else if (Boolean.TRUE.equals(request.removeAvatar())) {
            user.setAvatarUrl(null);
        }

        if (hasCover) {
            user.setCoverUrl(fileUploadService.upload(request.cover(), "covers/" + userId));
        } else if (Boolean.TRUE.equals(request.removeCover())) {
            user.setCoverUrl(null);
        }

        return ProfileResponse.from(user);
    }

    private boolean hasFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    private void validatePhoto(MultipartFile file) {
        if (file.getSize() > MAX_PHOTO_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A foto deve ter no máximo 5 MB");
        }

        var contentType = file.getContentType();
        if (contentType == null || !PHOTO_TYPES.contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use uma foto JPG, PNG ou GIF");
        }

        try (var input = file.getInputStream(); var stream = ImageIO.createImageInputStream(input)) {
            if (stream == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não foi possível ler a foto");
            }
            var readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O arquivo não é uma imagem válida");
            }
            var reader = readers.next();
            try {
                reader.setInput(stream, true, true);
                var format = reader.getFormatName().toLowerCase(Locale.ROOT);
                if (!contentType.equals("image/" + format)
                        || reader.getWidth(0) > MAX_PHOTO_DIMENSION || reader.getHeight(0) > MAX_PHOTO_DIMENSION) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A imagem é inválida ou excede 8000 pixels");
                }
            } finally {
                reader.dispose();
            }
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não foi possível ler a foto", exception);
        }
    }
}
