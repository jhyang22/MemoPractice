package com.sparta.memo.controller;

import com.sparta.memo.dto.MemoRequestDto;
import com.sparta.memo.dto.MemoResponseDto;
import com.sparta.memo.entity.Memo;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// 요구사항에 JSON을 통해 통신하라고 했으니 RestController 사용하면 된다!
// 특정 URL을 통할 것이므로 @RequsetMapping 사용
@RestController
@RequestMapping("/memos")
public class MemoController {

    // 실제로 데이터베이스에 저장하는게 아니라 자료구조를 사용해서 임시로 데이터를 저장할 것이므로 Map 사용!
    // 이렇게 초기화하면 memoList라는 Bean(Map 자료구조)이 생성됨
    private final Map<Long, Memo> memoList = new HashMap<>();

    // 실제로 호출하여 사용할 컨트롤러(API)
    // 리턴타입은 응답데이터이므로 MemoResponseDto!
    // 생성할 때 데이터를 줘도 되고 안줘도 되는데 그건 내 마음!
    // 클라이언트로부터 json 데이터를 전달받았을때 파라미터로 바로 바인딩할 수 있게 해주는게 @RequestBody!
    // 요청데이터 형식은 MemoRequestDto였으니 타입은 MemoRequestDto!
    // 생성하는 것이므로 PostMapping!
    @PostMapping
    public MemoResponseDto createMemo(@RequestBody MemoRequestDto dto) {

        // 식별자가 1씩 증가 하도록 만들어줘야함
        // isEmpty()는 memoList가 비어있는지 확인하는 메서드(true라면 비어있는 것!)
        // ?: 부분은 삼항 연산자이다. 조건 ? 값1 : 값2
        // 조건이 true라면 값1을 반환, false면 값2를 반환
        // Collections.max()는 ()의 값 중 최댓값을 뽑아내는 메서드
        // memoList.keySet()은 메모리스트 안에 있는 키값들을 모두 꺼내는 것(우리가 초기화할 때 Key는 Long 형태로 했었다!)
        // 즉, memoList가 비어있으면 1을 반환, 비어있지 않으면 memoList의 키값중에서 제일 큰 것에 1을 더한값을 반환!
        // 여기서 1을 더하는 이유는 게시글이 늘어나면 당연히 게시글 번호가 1 늘어날 것이므로!
        Long memoId = memoList.isEmpty() ? 1 : Collections.max(memoList.keySet()) + 1;

        // 요청받은 데이터로 Memo 객체 생성
        // 요청받은 데이터들은 dto안에 있으므로 dto의 메서드를 이용한다
        Memo memo = new Memo(memoId, dto.getTitle(), dto.getContents());

        // Inmemory DB에 Memo 메모
        // Inmemory DB는 우리가 데이터베이스에 저장해서 쓰는게 아닌 Map을 사용해서 프로그램 실행 중에만 사용하는 데이터베이스라고 생각
        memoList.put(memoId, memo);

        // Response 객체로 변환해서 반환!
        return new MemoResponseDto(memo);
    }


    // 조회하는 기능이므로 GetMapping 사용
    // id를 통해 조회할 것이므로 식별자를 둔다
    // 식별자를 받아오므로 @PathVariable 사용
    @GetMapping("/{id}")
    public MemoResponseDto findMemoById(@PathVariable Long id) {

        // 메모리스트에서 id를 통해 조회하는 것
        Memo memo = memoList.get(id);

        return new MemoResponseDto(memo);
    }


    // 수정하는 기능이므로 PutMapping
    // id를 통해 수정할 것이므로 식별자(경로변수)를 둔다
    // 경로변수가 있으므로 PathVariable
    // 데이터는 RequestDto로 받기로 하였으므로 수정 데이터도 RequestDto로 받는다!
    // Put은 Body와 연관되므로 @RequestBody 사용!
    // 즉, 수정할 경로는 @PathVariable로 지정, 데이터는 @RequestBody로 지정한 후 그것 파라미터로 넣어준다!
    @PutMapping("/{id}")
    public MemoResponseDto updateMemoById(
        @PathVariable Long id,
        @RequestBody MemoRequestDto dto
    ) {
        Memo memo = memoList.get(id);

        memo.update(dto);

        return new MemoResponseDto(memo);
    }



    // 삭제하는 데이터이므로 return 없다
    @DeleteMapping("/{id}")
    public void deleteMemo(@PathVariable Long id) {
        memoList.remove(id);
    }
}
