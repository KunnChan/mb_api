package com.music.kevinmusic.controller;

import com.music.kevinmusic.command.SongCommand;
import com.music.kevinmusic.common.CustomCommon;
import com.music.kevinmusic.domain.Information;
import com.music.kevinmusic.domain.Song;
import com.music.kevinmusic.request.SongRequest;
import com.music.kevinmusic.request.SongSingleRequest;
import com.music.kevinmusic.service.SongService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class SongController {

    private final SongService songService;

    @Autowired
    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping("/song/{id}")
    public Song get(@PathVariable Long id, @RequestHeader MultiValueMap<String, String> headers){

        log.info("song id " + id);
        Information information = CustomCommon.getBrowserInformation(headers);
        return songService.getSongById(id, information);
    }

    @PostMapping("/song/q")
    public Page<Song> get(@RequestBody SongSingleRequest songSingleRequest,
                          @RequestHeader MultiValueMap<String, String> headers){

        log.info("song specific query : {}, ", songSingleRequest);
        songSingleRequest.setInformation(CustomCommon.getBrowserInformation(headers));
        return songService.getFilterOneQuery(songSingleRequest);
    }

    @PostMapping("/song/query")
    public Page<Song> get(@RequestBody SongRequest songRequest,
                          @RequestHeader MultiValueMap<String, String> headers){

        log.info("song advance query => {}", songRequest);
        songRequest.setInformation(CustomCommon.getBrowserInformation(headers));
        return songService.getFilter(songRequest);
    }

    @PostMapping("/shield/song/save")
    public Song saveOrUpdate(@RequestBody SongCommand songCommand,
                             @RequestHeader MultiValueMap<String, String> headers){

        log.info("song saveOrUpdate => {}", songCommand);
        Information information = CustomCommon.getBrowserInformation(headers);
        return songService.saveOrUpdate(songCommand, information);
    }

    /**
     * must have admin access
     * @param songCommand must include String comment
     * @param headers
     * @return
     */
    @PostMapping("/shield/song/comment")
    public Song addComment(@RequestBody SongCommand songCommand,
                           @RequestHeader MultiValueMap<String, String> headers){

        log.info("song giveComment => {}", songCommand);
        Information information = CustomCommon.getBrowserInformation(headers);
        return songService.addComment(songCommand, information);
    }

    @PostMapping("/shield/song/reaction")
    public Song addReaction(@RequestBody SongCommand songCommand,
                           @RequestHeader MultiValueMap<String, String> headers){

        log.info("song giveComment => {}", songCommand);
        Information information = CustomCommon.getBrowserInformation(headers);
        return songService.addReaction(songCommand, information);
    }

    /**
     * must have admin access
     * @param songCommand must include Set<DownloadLink> downloadLinks
     * @param headers
     * @return
     */
    @PostMapping("/shield/song/link/add")
    public Song addDownloadLinks(@RequestBody SongCommand songCommand,
                                 @RequestHeader MultiValueMap<String, String> headers){

        log.info("add download links => {}", songCommand);
        Information information = CustomCommon.getBrowserInformation(headers);
        return songService.addDownloadLinks(songCommand, information);
    }


    /**
     * must have admin access
     * @param songCommand must include DownloadLink downloadLink
     * @param headers
     * @return
     */
    @PostMapping("/zone/song/link/remove")
    public Song removeDownloadLink(@RequestBody SongCommand songCommand,
                                   @RequestHeader MultiValueMap<String, String> headers){

        log.info("remove download link => {}", songCommand);
        Information information = CustomCommon.getBrowserInformation(headers);
        return songService.removeDownloadLink(songCommand, information);
    }

    /**
     * must have admin access
     * @param songCommand must include DownloadLink downloadLink
     * @param headers
     * @return
     */
    @PostMapping("/zone/song/lyrics/add")
    public Song addLyric(@RequestBody SongCommand songCommand,
                                   @RequestHeader MultiValueMap<String, String> headers){

        log.info("add song lyric => {}", songCommand);
        Information information = CustomCommon.getBrowserInformation(headers);
        return songService.addSongLyric(songCommand, information);
    }
}
