package com.adeptj.modules.jaxrs.consumer;

import com.adeptj.modules.data.jpa.JpaRepository;
import com.adeptj.modules.data.jpa.JpaUnitName;
import com.adeptj.modules.data.jpa.core.AbstractJpaRepository;
import com.adeptj.modules.jaxrs.consumer.entity.User;
import org.osgi.service.component.annotations.Component;

@JpaUnitName(name = "AdeptJ_PU_MySQL")
@Component(service = {JpaRepository.class, UserRepository.class}, immediate = true)
public class UserRepository extends AbstractJpaRepository<User, Long> {

}
