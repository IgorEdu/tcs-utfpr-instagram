package com.utfpr.tcs.instagram.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ListagemPostsResponseDTO {
    private List<PostResponseDTO> posts;
}
