package com.acxiom.interview.repository.elasticsearch.user;

import com.acxiom.interview.domain.elasticsearch.user.UserIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserIndexRepository extends ElasticsearchRepository<UserIndex, Long>{
}
