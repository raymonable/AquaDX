package icu.samnyan.aqua.sega.ongeki.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import icu.samnyan.aqua.sega.ongeki.handler.impl.*;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author samnyan (privateamusement@protonmail.com)
 */
@RestController
@RequestMapping("/g/ongeki")
@AllArgsConstructor
public class OngekiController {
    private final GetUserItemHandler getUserItemHandler;
    private final GetUserMusicHandler getUserMusicHandler;
    private final GetUserPreviewHandler getUserPreviewHandler;
    private final GetUserRecentRatingHandler getUserRecentRatingHandler;
    private final GetUserRivalMusicHandler getUserRivalMusicHandler;
    private final GetUserRivalDataHandler getUserRivalDataHandler;
    private final GetUserKopHandler getUserKopHandler;
    private final UpsertUserAllHandler upsertUserAllHandler;

    @PostMapping("GetUserItemApi")
    public String getUserItem(@ModelAttribute Map<String, Object> request) throws JsonProcessingException {
        return getUserItemHandler.handle(request);
    }

    @PostMapping("GetUserMusicApi")
    public String getUserMusic(@ModelAttribute Map<String, Object> request) throws JsonProcessingException {
        return getUserMusicHandler.handle(request);
    }

    @PostMapping("GetUserPreviewApi")
    public String getUserPreview(@ModelAttribute Map<String, Object> request) throws JsonProcessingException {
        return getUserPreviewHandler.handle(request);
    }

    @PostMapping("GetUserRecentRatingApi")
    public String getUserRecentRating(@ModelAttribute Map<String, Object> request) throws JsonProcessingException {
        return getUserRecentRatingHandler.handle(request);
    }

    @PostMapping("GetUserRivalDataApi")
    public String getUserRivalData(@ModelAttribute Map<String, Object> request) throws JsonProcessingException {
        return getUserRivalDataHandler.handle(request);
    }

    @PostMapping("GetUserRivalMusicApi")
    public String getUserRivalMusic(@ModelAttribute Map<String, Object> request) throws JsonProcessingException {
        return getUserRivalMusicHandler.handle(request);
    }

    @PostMapping("GetUserKopApi")
    public String getUserKop(@ModelAttribute Map<String, Object> request) throws JsonProcessingException {
        return getUserKopHandler.handle(request);
    }

    @PostMapping("UpsertUserAllApi")
    public String upsertUserAll(@ModelAttribute Map<String, Object> request) throws JsonProcessingException {
        return upsertUserAllHandler.handle(request);
    }
}
