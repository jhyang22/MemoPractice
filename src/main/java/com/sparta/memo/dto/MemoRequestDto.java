package com.sparta.memo.dto;

import lombok.Getter;

@Getter
public class MemoRequestDto {

    // 메모를 생성하기 위해서 클라이언트로부터 요청 받아야할 데이터는 title과 contents! (id는 서버에서 관리하면 됨)
    private String title;
    private String contents;
}
