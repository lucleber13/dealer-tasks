package com.cbcode.dealertasks.Valet.repository;

import com.cbcode.dealertasks.Valet.model.Valet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ValetRepository extends JpaRepository<Valet, Long> {

    @Query("SELECT v FROM Valet v WHERE v.user.id = :user_id")
    Optional<Valet> findByUser_Id(@Param("user_id") Long user);
}
