package in.canaris.cloud.repository;


import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.AppRole;
import in.canaris.cloud.entity.AppUser;

@Repository
@Transactional
public interface AppRoleRepository extends JpaRepository<AppRole, Integer> {

	AppRole findByRoleId(Long roleId);
}
