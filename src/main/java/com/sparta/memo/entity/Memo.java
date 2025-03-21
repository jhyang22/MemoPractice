package com.sparta.memo.entity;

import com.sparta.memo.dto.MemoRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Memo {
    private Long id;
    private String title;
    private String contents;


    // 굳이 return이 필요없으므로 void 사용
    // 요청 데이터를 받아와야하므로 파라미터에 넣어준다
    public void update(MemoRequestDto dto) {
        this.title = dto.getTitle();
        this.contents = dto.getContents();
    }
}
