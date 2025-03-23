package com.sparta.memo.dto;

import com.sparta.memo.entity.Memo;
import lombok.Getter;

@Getter
public class MemoResponseDto {

    // 메모라는 객체가 생성되고나서 저장될 때는 식별자가 존재할 것이다!
    // 다만 이건 요구사항에 따라 다름. 응답데이터를 보낼 때 id가 필요하다면 포함시키고 아니면 말기!
    private Long id;
    private String title;
    private String contents;

    // 생성자를 만들어주는데 파라미터로 Memo 타입을 받으면 아주 편하다!
    // 메모 객체가 그대로 반환되는게 아니라 Response 객체로 바뀌어서 반환되어야 한다!
    public MemoResponseDto(Memo memo) {
        this.id = memo.getId();
        this.title = memo.getTitle();
        this.contents = memo.getContents();
    }
}

