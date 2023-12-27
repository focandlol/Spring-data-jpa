package study.datajpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id){
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member){
        //Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<MemberDto> list(Pageable pageable){
        Page<Member> page = memberRepository.findAll(pageable);
        Page<MemberDto> map = page.map(o -> new MemberDto(o));
        System.out.println("pageable.getOffset() = " + pageable.getOffset());
        System.out.println("pageable.getPageNumber() = " + pageable.getPageNumber());

        return map;
    }

    @PostConstruct
    public void init(){
        for(int i=0; i<100; i++){
            memberRepository.save(new Member("user"+i,i));
        }
    }
}
