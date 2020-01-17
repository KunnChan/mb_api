package com.music.kevinmusic.command;

import com.music.kevinmusic.domain.DownloadLink;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@ToString
public class SongCommand {

    private Long id;
    private String photoLink;
    private String title;
    private String genre;
    private String artist;
    private String information;
    private String album;
    private String language;
    private Date createdAt;
    private String createdBy;
    private Date updatedAt;
    private String updatedBy;

    private String browserInfo;

    private Set<DownloadLink> downloadLinks = new HashSet<>();
}