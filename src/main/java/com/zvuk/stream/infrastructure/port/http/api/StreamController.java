package com.zvuk.stream.infrastructure.port.http.api;

import com.zvuk.stream.ApiResponse;
import com.zvuk.stream.domain.entities.Track;
import com.zvuk.stream.infrastructure.common.SoundReader;
import com.zvuk.stream.infrastructure.port.dto.SoundMapDTO;
import com.zvuk.stream.infrastructure.services.TrackService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(
        value = "/api/stream/sound/",
        produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class StreamController {

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    TrackService trackService;

    @RequestMapping(
            value = "chunk",
            method = RequestMethod.GET
    )
    public ResponseEntity<byte[]> getSoundChunk(@RequestHeader Map<String, String> headers) {
        Map<Integer, String> songMap = new HashMap<>();

        songMap.put(0, null);
        songMap.put(1, "0-241750");
        songMap.put(2, "241751-483500");
        songMap.put(3, "483501-725250");
        songMap.put(4, "725251-967000");
        songMap.put(5, "967001-1208750");
        songMap.put(6, "1208751-1450500");
        songMap.put(7, "1450501-1692250");
        songMap.put(8, "1692251-1934000");
        songMap.put(9, "1934001-2175750");
        songMap.put(10, "2175751-2417500");
        songMap.put(11, "2417501-2659250");
        songMap.put(12, "2659251-2901000");
        songMap.put(13, "2901001-3287803");


        Integer index = (headers.containsKey("accept-ranges")) ? Integer.parseInt(headers.get("accept-ranges")) : 0;

        SoundReader soundReader = new SoundReader();
        return soundReader.prepareContent("sound", "mp3", songMap.get(index));
    }

    @RequestMapping(
            value = "map",
            method = RequestMethod.GET
    )
    public ApiResponse getSoundMap(@RequestParam int track_id, @RequestParam int user_id) throws IOException {

        System.out.println(track_id);
        System.out.println(user_id);

        Track track = trackService.getTrackByIdAndUserId(track_id, user_id);


        SoundReader soundReader = new SoundReader();
        SoundMapDTO soundMap = soundReader.getSoundMap(track);

        return ApiResponse.buildResponseObject(HttpStatus.OK, soundMap, null);
    }

    @RequestMapping(
            value = "upload",
            method = RequestMethod.POST,
            consumes = {"multipart/form-data"}
    )
    public ApiResponse upload(@RequestParam("file") MultipartFile file) {

        System.out.println(file.getContentType());
        System.out.println(file.getName());
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getSize());

        String folderPath = "/files/sounds/" + RandomStringUtils.random(8, true, true);

        File theDir = new File("/Users/artem.teslenko/Work/My_Projects/java-stream" + folderPath);
        System.out.println(theDir.getAbsolutePath());
        if (!theDir.exists()){
            boolean mkdirs = theDir.mkdirs();
            if (mkdirs) {
                System.out.println("Directory created successfully");
            } else {
                System.out.println("Sorry couldn’t create specified directory");
            }
        }

        String filePath = theDir.getPath() + "/" + file.getOriginalFilename();
        File transferFile = new File(filePath);

        try {
            file.transferTo(transferFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ApiResponse.buildResponseObject(HttpStatus.OK, null, null);
    }

    @RequestMapping(
            value = "list",
            method = RequestMethod.POST
    )
    public ApiResponse list() {
        return ApiResponse.buildResponseObject(HttpStatus.OK, null, null);
    }

}
