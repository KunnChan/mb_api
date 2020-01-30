package com.music.kevinmusic.service.impl;

import com.google.gson.Gson;
import com.music.kevinmusic.command.SongCommand;
import com.music.kevinmusic.common.CustomCommon;
import com.music.kevinmusic.domain.*;
import com.music.kevinmusic.enums.EventAction;
import com.music.kevinmusic.exception.NotFoundException;
import com.music.kevinmusic.filter.QSong;
import com.music.kevinmusic.repository.SongRepository;
import com.music.kevinmusic.repository.TransactionHistoryRepository;
import com.music.kevinmusic.request.SongRequest;
import com.music.kevinmusic.request.SongSingleRequest;
import com.music.kevinmusic.service.SongService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final TransactionHistoryRepository historyRepo;
    private ModelMapper modelMapper;
    private Gson gson;

    @Autowired
    public SongServiceImpl(SongRepository songRepository, TransactionHistoryRepository historyRepo) {
        this.songRepository = songRepository;
        this.historyRepo = historyRepo;
        modelMapper = new ModelMapper();
        gson = new Gson();
    }

    @Override
    @Transactional
    public Song getSongById(Long id, Information information) {
        TransactionHistory history = new TransactionHistory("Song Id : " + id, EventAction.SEARCH_SONG_BY_ID);
        history.setInformation(information);
        historyRepo.save(history);

        return songRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Song id not found : " + id));
    }

    @Override
    @Transactional
    public Page<Song> getFilterOneQuery(SongSingleRequest songSingleRequest) {

        PageRequest pageable = CustomCommon.getPageable(songSingleRequest.getPage());
        BooleanExpression filter = getQuery(songSingleRequest.getQuery());

        TransactionHistory history = new TransactionHistory(gson.toJson(songSingleRequest), EventAction.SEARCH_SONG_BY_SINGLE_QUERY);
        history.setInformation(songSingleRequest.getInformation());
        historyRepo.save(history);

        if(filter == null) return songRepository.findAll(pageable);

        return songRepository.findAll(filter, pageable);
    }

    @Override
    @Transactional
    public Page<Song> getFilter(SongRequest songRequest) {
        PageRequest pageable = CustomCommon.getPageable(songRequest.getPage());
        List<BooleanExpression> filters = getQuery(songRequest);

        TransactionHistory history = new TransactionHistory(gson.toJson(songRequest), EventAction.SEARCH_SONG_ADVANCE);
        history.setInformation(songRequest.getInformation());
        historyRepo.save(history);

        if(filters.isEmpty()){
            return songRepository.findAll(pageable);
        }
        BooleanExpression filterExpression = filters.get(0);
        for (int i = 1; i <= filters.size() - 1; ++i) {
            filterExpression = filterExpression.and(filters.get(i));
        }
        return songRepository.findAll(filterExpression, pageable);
    }

    @Override
    @Transactional
    public Song saveOrUpdate(SongCommand songCommand, Information information) {
        Song song = modelMapper.map(songCommand, Song.class);
        TransactionHistory history = new TransactionHistory(gson.toJson(songCommand));
        history.setInformation(information);
        if(song.getId() == null){
            // insert
            history.setEventAction(EventAction.CREATE_SONG);
        }else{
            // update
          //  Song songForUpdate = songRepository.findById(song.getId())
          ///          .orElseThrow(() -> new NotFoundException("Song id not found for update : " + song.getId()));

            history.setEventAction(EventAction.UPDATE_SONG);
        }

        historyRepo.save(history);
        return songRepository.save(song);
    }

    @Override
    @Transactional
    public Song addComment(SongCommand songCommand, Information information) {

        TransactionHistory history = new TransactionHistory(gson.toJson(songCommand), EventAction.COMMENT_ADD);
        history.setInformation(information);

        Song song = songRepository.findById(songCommand.getId())
                .orElseThrow(() -> new NotFoundException("Song id not found : " + songCommand.getId()));

        song.addComment(new Comment(songCommand.getComment()));

        historyRepo.save(history);
        return songRepository.save(song);
    }

    @Transactional
    @Override
    public Song addDownloadLinks(SongCommand songCommand, Information information) {
        TransactionHistory history = new TransactionHistory(gson.toJson(songCommand), EventAction.DOWNLOAD_LINK_ADD);
        history.setInformation(information);

        Song song = songRepository.findById(songCommand.getId())
                .orElseThrow(() -> new NotFoundException("Song id not found : " + songCommand.getId()));

        song.addDownloadLinks(songCommand.getDownloadLinks());

        historyRepo.save(history);
        return songRepository.save(song);
    }

    @Transactional
    @Override
    public Song removeDownloadLink(SongCommand songCommand, Information information) {
        TransactionHistory history = new TransactionHistory(gson.toJson(songCommand), EventAction.DOWNLOAD_LINK_REMOVE);
        history.setInformation(information);

        Song song = songRepository.findById(songCommand.getId())
                .orElseThrow(() -> new NotFoundException("Song id not found : " + songCommand.getId()));

        song.removeDownloadLink(songCommand.getDownloadLink());

        historyRepo.save(history);
        return songRepository.save(song);
    }

    @Transactional
    @Override
    public Song addSongLyric(SongCommand songCommand, Information information) {
        TransactionHistory history = new TransactionHistory(gson.toJson(songCommand), EventAction.LYRIC_UPDATE);
        history.setInformation(information);

        Song song = songRepository.findById(songCommand.getId())
                .orElseThrow(() -> new NotFoundException("Song id not found : " + songCommand.getId()));

        song.setLyrics(new Lyrics(songCommand.getLyrics()));

        historyRepo.save(history);
        return songRepository.save(song);
    }

    @Transactional
    @Override
    public Song addDownload(SongCommand songCommand, Information information) {
        TransactionHistory history = new TransactionHistory(gson.toJson(songCommand), EventAction.LYRIC_UPDATE);
        history.setInformation(information);

        Song song = songRepository.findById(songCommand.getId())
                .orElseThrow(() -> new NotFoundException("Song id not found : " + songCommand.getId()));

        song.addDownload(new Download(songCommand.getUserInfo()));

        historyRepo.save(history);
        return songRepository.save(song);
    }

    @Override
    public List<Song> getTop15Album(Information information) {

        return null;
    }

    @Override
    public Page<Song> getPopularSong(SongSingleRequest songSingleRequest) {

        return null;
    }

    @Transactional
    @Override
    public Song addReaction(SongCommand songCommand, Information information) {
        TransactionHistory history = new TransactionHistory(gson.toJson(songCommand), EventAction.REACTION_ADD);
        history.setInformation(information);

        Song song = songRepository.findById(songCommand.getId())
                .orElseThrow(() -> new NotFoundException("Song id not found : " + songCommand.getId()));

        Reaction reaction = new Reaction(songCommand.getUserReaction());
        song.addReaction(reaction);

        historyRepo.save(history);
        return songRepository.save(song);
    }

    private BooleanExpression getQuery(String query) {
        if(query == null) return null;

       QSong songQuery = QSong.songEntity;
       BooleanExpression filter = songQuery.album.equalsIgnoreCase(query)
                .or(songQuery.artist.equalsIgnoreCase(query))
                .or(songQuery.genre.equalsIgnoreCase(query))
                .or(songQuery.information.likeIgnoreCase(query))
                .or(songQuery.language.equalsIgnoreCase(query))
                .or(songQuery.title.equalsIgnoreCase(query));

        return filter;
    }

    private List<BooleanExpression> getQuery(SongRequest songRequest) {
        QSong songQuery = QSong.songEntity;
        List<BooleanExpression> filters = new ArrayList<>();
        if(songRequest.getId() != null){
            filters.add(songQuery.id.eq(songRequest.getId()));
        }
        if(songRequest.getTitle() != null){
            filters.add(songQuery.title.equalsIgnoreCase(songRequest.getTitle()));
        }
        if(songRequest.getGenre() != null){
            filters.add(songQuery.genre.equalsIgnoreCase(songRequest.getGenre()));
        }
        if(songRequest.getArtist() != null){
            filters.add(songQuery.artist.equalsIgnoreCase(songRequest.getArtist()));
        }
        if(songRequest.getAlbum() != null){
            filters.add(songQuery.album.equalsIgnoreCase(songRequest.getAlbum()));
        }
        if(songRequest.getLanguage() != null){
            filters.add(songQuery.language.equalsIgnoreCase(songRequest.getLanguage()));
        }
        if(songRequest.getInfo() != null){
            filters.add(songQuery.information.in(songRequest.getInfo()));
        }
        return filters;
    }

}
