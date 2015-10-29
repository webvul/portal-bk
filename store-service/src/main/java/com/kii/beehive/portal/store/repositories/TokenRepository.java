package com.kii.beehive.portal.store.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.kii.beehive.portal.store.entity.LandLord;
import com.kii.beehive.portal.store.entity.TokenStore;

@Component
public interface TokenRepository extends CrudRepository<TokenStore, String> {


}
