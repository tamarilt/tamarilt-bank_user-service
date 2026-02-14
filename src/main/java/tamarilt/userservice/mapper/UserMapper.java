package tamarilt.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import tamarilt.userservice.dto.request.RegistrationRequestDto;
import tamarilt.userservice.dto.response.TokensResponseDto;
import tamarilt.userservice.dto.response.RegistrationResponseDto;
import tamarilt.userservice.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(RegistrationRequestDto dto);
    

    @Mapping(target = "accessToken", source = "accessToken")
    @Mapping(target = "refreshToken", source = "refreshToken")
    RegistrationResponseDto toResponseDto(User user, String accessToken, String refreshToken);
    
    TokensResponseDto toLoginResponseDto(String accessToken, String refreshToken);
}
