package com.sparta.memo.controller;

import com.sparta.memo.dto.MemoRequestDto;
import com.sparta.memo.dto.MemoResponseDto;
import com.sparta.memo.entity.Memo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    // -- 메모장 프로젝트 내용--
    // ResponseEntitiy<>는 HTTP Response 상태 코드 설정이 가능하게 해주는 것. HTTPEntity를 상속받은 객체
    @PostMapping
    public ResponseEntity<MemoResponseDto> createMemo(@RequestBody MemoRequestDto dto) {

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
        // -- 메모장 프로젝트 내용 --
        // ResponseEntity<> 로 타입을 변경해줬으므로 리턴도 바꿔준다
        // httpStatus 이넘 중 하나인 CREATED를 이용! Postman을 보면 200으로 출력되던게 201로 변경된 걸 알 수 있다
        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.CREATED);
    }


    // -- 메모장 프로젝트 내용 --
    // 원래는 위에서 응답타입을 ResponseEntity로 바꿨으므로 모두 ResponseEntity로 하는게 좋지만 강의에서는 예시로 List로 함 -> 후에 내가 ResponseEntity로 수정함~
    // @GetMapping에 아무것도 적지 않으면 최상단에 위치한 URL로 접근한다!
    // 전체 데이터 조회이므로 findAllMemos()의 파라미터는 필요없다
    @GetMapping
    public ResponseEntity<List<MemoResponseDto>> findAllMemos() {

        // init List (리스트형태로 데이터를 응답해주기 위해 리스트를 초기화해준다)
        List<MemoResponseDto> responseList = new ArrayList<>();

        // HashMap<Memo> -> List<MemoResponseDto>
        // 우리가 자료를 HashMap의 형태로 보관하고있으므로 이걸 전체조회하기위해 List형태로 바꿔줘야한다
        // 1. for문을 활용한 방법
        for (Memo memo : memoList.values()) {
            MemoResponseDto responseDto = new MemoResponseDto(memo);
            responseList.add(responseDto);
        }

        // Map To List
        // 2. Stream 사용방법
//        responseList = memoList.values().stream().map(MemoResponseDto::new).toList();

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }


    // 조회하는 기능이므로 GetMapping 사용
    // id를 통해 조회할 것이므로 식별자를 둔다
    // 식별자를 받아오므로 @PathVariable 사용
    // -- 메모장 프로젝트 내용 --
    // 위에서 ResponseEntity로 변경해줬으므로 여기도 변경해준다
    @GetMapping("/{id}")
    public ResponseEntity<MemoResponseDto> findMemoById(@PathVariable Long id) {

        // 메모리스트에서 id를 통해 조회하는 것
        Memo memo = memoList.get(id);

        // 조회된 데이터가 없을 경우 동적으로 NOT_FOUND 메시지!
        if (memo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.OK);
    }


    // 수정하는 기능이므로 PutMapping
    // id를 통해 수정할 것이므로 식별자(경로변수)를 둔다
    // 경로변수가 있으므로 PathVariable
    // 데이터는 RequestDto로 받기로 하였으므로 수정 데이터도 RequestDto로 받는다!
    // Put은 Body와 연관되므로 @RequestBody 사용!
    // 즉, 수정할 경로는 @PathVariable로 지정, 데이터는 @RequestBody로 지정한 후 그것 파라미터로 넣어준다!
    // -- 메모장 프로젝트 내용 --
    // 여기도 ResponseEntity<>로 바꿔준다
    @PutMapping("/{id}")
    public ResponseEntity<MemoResponseDto> updateMemoById(
            @PathVariable Long id,
            @RequestBody MemoRequestDto dto
    ) {
        // 우선 메모를 조회하기 위해 알맞은 id값을 가져온다
        Memo memo = memoList.get(id);

        if (memo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 요구사항에 수정할 요청 데이터(제목, 내용)가 꼭 필요하다고 하였으므로 조건을 건다!
        if(dto.getTitle() == null || dto.getContents() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        memo.update(dto);

        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.OK);
    }


    // -- 메모장 프로젝트 내용 --
    // 메모 단 건 제목 수정 기능
    // 일부 수정이므로 Patch 사용
    @PatchMapping("/{id}")
    public ResponseEntity<MemoResponseDto> updateTitle(
            @PathVariable Long id,
            @RequestBody MemoRequestDto dto
    ) {
        Memo memo = memoList.get(id);

        // NPE 방지(PUT과 같음)
        if (memo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // PUT과는 다르게 제목만 수정하는 기능이므로 내용은 존재하는데 제목이 null일 경우 Bad_request를 반환!
        if(dto.getTitle() == null || dto.getContents() != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        memo.updateTitle(dto);

        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.OK);
    }


    // 삭제하는 데이터이므로 return 없다
    // -- 메모장 프로젝트 내용 --
    // ResponseEntity로 바꿔주는데 이 경우 void가 아닌 Void를 사용한다
    // Void는 java에서 제공하는 클래스로 제네릭 타입에서 "반환 타입이 없을 때" 사용된다
    // 여기가 ResponseEntity<> 를 해주기 전까진 void 형태라 return이 없었는데 ResponseEntity<Void>가 되며 return이 생겼다
    // Void는 void와 다르게 return null을 해줘야 하는데 여기에선 실제 데이터 없이 HTTP 응답 코드만 반환한다.
    // 즉, Void 자체는 의미가 없고 ResponseEntity가 중요!
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemo(@PathVariable Long id) {

        // memoList의 Key값에 id를 포함하고 있다면
        if(memoList.containsKey(id)) {
            memoList.remove(id);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

// 수정했음에도 발생하는 문제점
// 컨트롤러에서 너무 많은 책임을 가지고 있다 (데이터 저장, 응답, 요청 처리 등)
// 데이터베이스가 없다(프로그램 종료 시 데이터가 모두 삭제됨