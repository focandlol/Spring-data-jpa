package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface MemberProjection {

    Long getId();
    @Value("#{target.username + ' ' + target.age}")
    String getUsername();
    String getTeamName();
}
