package com.ssafy.api.controller;

import com.ssafy.api.request.user.UserDetailPutReq;
import com.ssafy.api.request.user.UserTimeLogReq;
import com.ssafy.api.response.user.UserInfoRes;
import com.ssafy.api.service.BadgeService;
import com.ssafy.api.service.UserService;
import com.ssafy.common.model.response.BaseResponse;
import com.ssafy.common.model.response.BaseResponseBody;
import com.ssafy.db.entity.User;
import com.ssafy.db.entity.UserImg;
import com.ssafy.db.entity.UserTimeLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

/**
 * 유저 관련 API 요청 처리를 위한 컨트롤러 정의
 */
@CrossOrigin("*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final BadgeService badgeService;

    /**
     * 사용자 정보 조회 API([GET] /api/v1/users/{email})
     */
    @Operation(summary = "이메일을 통한 사용자 정보 조회")
    @GetMapping("/{email}")
    public ResponseEntity<? extends BaseResponseBody> getUser(@PathVariable String email) {
        User user = userService.getUser(email);

        return ResponseEntity.ok().body(new BaseResponseBody(200, "Success"));
    }

    /**
     * 비밀번호 재설정 API([PUT] /api/v1/users/pass)
     */
    @Operation(summary = "비밀번호 재설정")
    @PutMapping("/pass")
    public ResponseEntity<? extends BaseResponseBody> updateUserPassword(@RequestBody Map<String, String> userInfo) {
        userService.updateUserPassword(userInfo);

        return ResponseEntity.ok().body(new BaseResponseBody(200, "Success"));
    }

    /**
     * 회원탈퇴 API([DELETE] /api/v1/users)
     */
    @Operation(summary = "회원 탈퇴")
    @DeleteMapping
    public ResponseEntity<? extends BaseResponseBody> deleteUser(@AuthenticationPrincipal String email) {
        userService.deleteUser(email);

        return ResponseEntity.ok().body(new BaseResponseBody(200, "Success"));
    }

    /**
     * 사용자 정보 조회 API([GET] /api/v1/users)
     */
    @Operation(summary = "사용자 정보 조회")
    @ApiResponse(responseCode = "200", description = "사용자 정보 조회")
    @GetMapping
    public ResponseEntity<? extends BaseResponse> findUserDetail(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(new BaseResponse<UserInfoRes>(200, "사용자 정보 조회 성공",
                userService.findByEmail(email)));
    }

    /**
     * 사용자 정보 수정 API([POST] /api/v1/users/{userId})
     */
    @Operation(summary = "사용자 정보 수정")
    @ApiResponse(responseCode = "200", description = "사용자 정보 수정")
    @PutMapping
    public ResponseEntity<?> updateUserDetail(@AuthenticationPrincipal String email, @RequestBody UserDetailPutReq userDetailPutReq) {
        return ResponseEntity.ok(new BaseResponse<User>(200, "사용자 정보 수정 성공",
                userService.updateUserDetail(userDetailPutReq, email)));
    }

    /**
     * 프로필 이미지 업로드 API([POST] /api/v1/users/image)
     */
    @Operation(summary = "프로필 이미지 업로드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 이미지 업로드 성공"),
            @ApiResponse(responseCode = "400", description = "올바르지 않은 파일")
    })
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@AuthenticationPrincipal String email,
                                         @RequestParam("image") MultipartFile multipartFile) throws IOException {
        if (userService.validImgFile(multipartFile)) {
            User user = userService.getUser(email);
            UserImg userImg = userService.uploadImage(multipartFile);
            user.setUserImg(userImg);
            userService.updateUser(user);
            return ResponseEntity.ok().body(new BaseResponseBody(200, "프로필 이미지 업로드 성공"));
        }
        return ResponseEntity.badRequest().body(new BaseResponseBody(400, "올바른 이미지 파일이 아닙니다"));
    }

    /**
     * 프로필 이미지 조회 API([GET] /api/v1/users/image)
     */
    @Operation(summary = "프로필 이미지 조회")
    @ApiResponse(responseCode = "200", description = "프로필 이미지 조회 성공")
    @GetMapping("/image")
    public ResponseEntity<?> getImage(@AuthenticationPrincipal String email) throws IOException {
        UserImg userImg = userService.getImage(email);
        Resource resource = new FileSystemResource(userImg.getFileUrl());
        HttpHeaders header = new HttpHeaders();
        Path filePath = Paths.get(userImg.getFileUrl());
        header.add("Content-Type", Files.probeContentType(filePath));

        return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
    }

    /**
     * 프로필 이미지 수정 API([PUT] /api/v1/users/image)
     */
    @Operation(summary = "프로필 이미지 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 이미지 수정 성공"),
            @ApiResponse(responseCode = "400", description = "올바르지 않은 파일")
    })
    @PutMapping("/image")
    public ResponseEntity<?> updateImage(@AuthenticationPrincipal String email,
                                         @RequestParam("image") MultipartFile multipartFile) throws IOException {
        if (userService.validImgFile(multipartFile)) {
            User user = userService.getUser(email);
            userService.updateImage(multipartFile, user);
            userService.updateUser(user);
            return ResponseEntity.ok().body(new BaseResponseBody(200, "프로필 이미지 업데이트 성공"));
        }
        return ResponseEntity.badRequest().body(new BaseResponseBody(400, "올바른 이미지 파일이 아닙니다"));
    }

    /**
     * 프로필 이미지 삭제 API([DELETE] /api/v1/users/image)
     */
    @Operation(summary = "프로필 이미지 삭제")
    @ApiResponse(responseCode = "200", description = "프로필 이미지 삭제 성공")
    @DeleteMapping("/image")
    public ResponseEntity<BaseResponseBody> deleteImage(@AuthenticationPrincipal String email) {
        userService.deleteImage(email);
        return ResponseEntity.ok().body(new BaseResponseBody(200, "프로필 이미지 삭제 성공"));
    }

    /**
     * 사용자 공부 시간 기록 조회 API([GET] /api/v1/users/log)
     */
    @Operation(summary = "사용자 공부 시간 기록 조회")
    @ApiResponse(responseCode = "200", description = "사용자 공부 시간 기록 생성 조회")
    @GetMapping("/log")
    public ResponseEntity<?> findUserTimeLog(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok().body(new BaseResponse<List<UserTimeLog>>(200, "사용자 공부 시간 기록 생성 성공", userService.getUserTimeLog(email)));
    }

    /**
     * 사용자 공부 시간 기록 수정 API([PUT] /api/v1/users/log)
     */
    @Operation(summary = "사용자 공부 시간 기록 생성 및 수정")
    @ApiResponse(responseCode = "200", description = "사용자 공부 시간 기록 생성 및 수정 성공")
    @PutMapping("/log")
    public ResponseEntity<?> updateUserTimeLog(@AuthenticationPrincipal String email, @RequestBody UserTimeLogReq userTimeLogReq) {
        Long diff = (userTimeLogReq.getEndTime() - userTimeLogReq.getStartTime()) / 1000;
        Instant startInstant = Instant.ofEpochMilli(userTimeLogReq.getStartTime());
        LocalDate day = LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault()).toLocalDate();
        UserTimeLog userTimeLog = userService.updateUserTimeLog(day, diff, email);
        badgeService.createTimeBadge(email);
        badgeService.createDayBadge(email);
        return ResponseEntity.ok().body(new BaseResponse<UserTimeLog>(200, "사용자 공부 시간 기록 생성 및 수정 성공", userTimeLog));
    }

    /**
     * 사용자 랭킹 집계 및 조회 API([GET] /api/v1/users/rank)
     */
    @Operation(summary = "사용자 랭킹 집계 및 조회")
    @ApiResponse(responseCode = "200", description = "사용자 랭킹 집계 및 조회 성공")
    @GetMapping("/rank")
    public ResponseEntity<?> findAllUserRank() {
        return ResponseEntity.ok(new BaseResponse<List<User>>(200, "사용자 랭킹 집계 및 조회 성공",
                userService.findAllUserRank()));
    }

    /**
     * 랭킹 내 프로필 이미지 조회 API([GET] /api/v1/users/rank/image)
     */
    @Operation(summary = "랭킹 내 프로필 이미지 조회")
    @ApiResponse(responseCode = "200", description = "랭킹 내 프로필 이미지 조회 성공")
    @GetMapping("/rank/image")
    public ResponseEntity<?> getUserRankImage(@RequestParam String email) throws IOException {
        UserImg userImg = userService.getImage(email);
        Resource resource = new FileSystemResource(userImg.getFileUrl());
        HttpHeaders header = new HttpHeaders();
        Path filePath = Paths.get(userImg.getFileUrl());
        header.add("Content-Type", Files.probeContentType(filePath));

        return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
    }

}
