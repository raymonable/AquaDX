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
    private final GetUserRatinglogListHandler getUserRatinglogListHandler;
    private final GetUserRecentRatingHandler getUserRecentRatingHandler;
    private final GetUserRegionHandler getUserRegionHandler;
    private final GetUserRivalHandler getUserRivalHandler;
    private final GetUserRivalMusicHandler getUserRivalMusicHandler;
    private final GetUserRivalDataHandler getUserRivalDataHandler;
    private final GetUserScenarioHandler getUserScenarioHandler;
    private final GetUserSkinHandler getUserSkinHandler;
    private final GetUserStoryHandler getUserStoryHandler;
    private final GetUserTechCountHandler getUserTechCountHandler;
    private final GetUserTradeItemHandler getUserTradeItemHandler;
    private final GetUserTrainingRoomByKeyHandler getUserTrainingRoomByKeyHandler;
    private final GetUserKopHandler getUserKopHandler;
    private final UpsertUserAllHandler upsertUserAllHandler;

    @PostMapping("GetUserTradeItemApi")
    public String getUserTradeItem(@ModelAttribute Map<String, Object> request) throws JsonProcessingException {
        return getUserTradeItemHandler.handle(request);
    }

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

    @PostMapping("GetUserRatinglogApi")
    public String getUserRatinglog(@ModelAttribute Map<String, Object> request) throws JsonProcessingException {
        return getUserRatinglogListHandler.handle(request);
    }

    @PostMapping("GetUserRecentRatingApi")
    public String getUserRecentRating(@ModelAttribute Map<String, Object> request) throws JsonProcessingException {
        return getUserRecentRatingHandler.handle(request);
    }

    @PostMapping("GetUserRegionApi")
    public String getUserRegion(@ModelAttribute Map<String, Object> request) throws JsonProcessingException {
        return getUserRegionHandler.handle(request);
    }

    @PostMapping("GetUserRivalApi")
    public String getUserRival(@ModelAttribute Map<String, Object> request) throws JsonProcessingException {
        return getUserRivalHandler.handle(request);
    }

    @PostMapping("GetUserRivalDataApi")
    public String getUserRivalData(@ModelAttribute Map<String, Object> request) throws JsonProcessingException {
        return getUserRivalDataHandler.handle(request);
    }

    @PostMapping("GetUserRivalMusicApi")
    public String getUserRivalMusic(@ModelAttribute Map<String, Object> request) throws JsonProcessingException {
        return getUserRivalMusicHandler.handle(request);
    }

    @PostMapping("GetUserScenarioApi")
    public String getUserScenario(@ModelAttribute Map<String, Object> request) throws JsonProcessingException {
        return getUserScenarioHandler.handle(request);
    }

    @PostMapping("GetUserSkinApi")
    public String getUserSkin(@ModelAttribute Map<String, Object> request) throws JsonProcessingException {
        return getUserSkinHandler.handle(request);
    }

    @PostMapping("GetUserStoryApi")
    public String getUserStory(@ModelAttribute Map<String, Object> request) throws JsonProcessingException {
        return getUserStoryHandler.handle(request);
    }

    @PostMapping("GetUserTechCountApi")
    public String getUserTechCount(@ModelAttribute Map<String, Object> request) throws JsonProcessingException {
        return getUserTechCountHandler.handle(request);
    }

    @PostMapping("GetUserTrainingRoomByKeyApi")
    public String getUserTrainingRoomByKey(@ModelAttribute Map<String, Object> request) throws JsonProcessingException {
        return getUserTrainingRoomByKeyHandler.handle(request);
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
