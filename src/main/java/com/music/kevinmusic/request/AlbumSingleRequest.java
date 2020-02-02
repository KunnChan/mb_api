package com.music.kevinmusic.request;

import com.music.kevinmusic.domain.Information;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AlbumSingleRequest {

    private String query;
    private PageInfo page;

    private Information information;
}