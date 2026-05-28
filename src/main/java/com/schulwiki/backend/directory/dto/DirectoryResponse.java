package com.schulwiki.backend.directory.dto;

import com.schulwiki.backend.record.dto.RecordLightResponse;
import com.schulwiki.backend.user.dto.UserLightResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DirectoryResponse {
    private Long id;
    private String name;
    private List<DirectoryLightResponse> subDirectories;
    private List<RecordLightResponse> records;
    private LocalDateTime createdAt;
    private UserLightResponse createdBy;
    private LocalDateTime updatedAt;
    private UserLightResponse updatedBy;
}
