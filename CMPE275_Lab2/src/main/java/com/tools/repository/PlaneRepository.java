package com.tools.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tools.model.Plane;

@Repository
public interface PlaneRepository extends JpaRepository<Plane,Integer> {
	List<Plane> findById(int id );
}

