package com.example.stripepayouts.Repository;

import com.example.stripepayouts.Entity.StripePayout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StripePayoutRepository extends JpaRepository<StripePayout, Long> {
    // Custom query methods can be defined here if needed
}
