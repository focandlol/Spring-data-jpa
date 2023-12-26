package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    EntityManager em;

    @Test
    public void testMember(){

        Member member = new Member("memberA");
        Member save = memberRepository.save(member);

        Member findMember = memberRepository.findById(save.getId()).get();

        assertThat(member).isEqualTo(findMember);
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(member1).isEqualTo(findMember1);
        assertThat(member2).isEqualTo(findMember2);

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        Long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member2);
        memberRepository.delete(member1);

        Long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThan(){
        Member member1 = new Member("memberA",10);
        Member member2 = new Member("memberA",20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("memberA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("memberA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
    }

    @Test
    public void testNamedQuery(){
        Member member1 = new Member("memberA",10);
        Member member2 = new Member("memberA",20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsername1("memberA");
        assertThat(result.get(0)).isEqualTo(member1);
    }

    @Test
    public void tesQuery(){
        Member member1 = new Member("memberA",10);
        Member member2 = new Member("memberA",20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("memberA",10);
        assertThat(result.get(0)).isEqualTo(member1);
    }

    @Test
    public void findUsernameList(){
        Member member1 = new Member("memberA",10);
        Member member2 = new Member("memberA",20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> usernameList = memberRepository.findUsernameList();
        assertThat(usernameList.get(0)).isEqualTo("memberA");
    }

    @Test
    public void findMemberDto(){

        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member member1 = new Member("memberA",10);
        member1.changeTeam(teamA);
        memberRepository.save(member1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames(){
        Member member1 = new Member("memberA",10);
        Member member2 = new Member("memberB",20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> byNames = memberRepository.findByNames(Arrays.asList("memberA", "memberB"));
        for (Member byName : byNames) {
            System.out.println("byName = " + byName);
        }
    }

    @Test
    public void returnType(){
        Member member1 = new Member("memberA",10);
        Member member2 = new Member("memberB",20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> memberA = memberRepository.findListByUsername("memberA");
        Member memberA1 = memberRepository.findMemberByUsername("memberA");
        Optional<Member> memberA2 = memberRepository.findOptionalByUsername("memberA");

    }

    @Test
    public void paging(){
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",10));
        memberRepository.save(new Member("member3",10));
        memberRepository.save(new Member("member4",10));
        memberRepository.save(new Member("member5",10));
        memberRepository.save(new Member("member6",10));
        memberRepository.save(new Member("member7",10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(1, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> page = memberRepository.findByAge(age,pageRequest);

        //api로 넘길 때 entity 그대로 넘기면 안됨, Page.map() 사용해서 dto로 변환후 넘길 것
        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);

        }
        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(3);
       // assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void Slice(){
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",10));
        memberRepository.save(new Member("member3",10));
        memberRepository.save(new Member("member4",10));
        memberRepository.save(new Member("member5",10));
        memberRepository.save(new Member("member6",10));
        memberRepository.save(new Member("member7",10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Slice<Member> page = memberRepository.findSliceByAge(age,pageRequest);

        List<Member> content = page.getContent();
        //long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);
        }
       // System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        //assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        //assertThat(page.getTotalPages()).isEqualTo(3);
        // assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test  //벌크 연산
    public void bulkUpdate(){
        em.setFlushMode(FlushModeType.COMMIT);
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",11));
        memberRepository.save(new Member("member3",12));
        memberRepository.save(new Member("member4",13));
        memberRepository.save(new Member("member5",14));
        em.flush();

        //int resultCount = memberRepository.bulkAgePlus(12);
        Optional<Member> byId = memberRepository.findById(5l);
        Member member1 = byId.get();
        System.out.println("member1 = " + member1);
        member1.setAge(1111);
        //em.flush();
        //em.clear();

        List<Member> result = memberRepository.findByUsername1("member5");
        Member member = result.get(0);
        System.out.println("member = " + member);

        //assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy(){

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTEAM() = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint(){
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    public void lock(){
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        List<Member> member11 = memberRepository.findLockByUsername("member1");

    }
}