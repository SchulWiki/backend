package com.schulwiki.backend.directory.mapper;

import com.schulwiki.backend.directory.dto.DirectoryLightResponse;
import com.schulwiki.backend.directory.dto.DirectoryResponse;
import com.schulwiki.backend.directory.entity.DirectoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DirectoryMapper {
    DirectoryResponse mapToResponse(DirectoryEntity entity);

    DirectoryLightResponse mapToListResponse(DirectoryEntity entity);
}
