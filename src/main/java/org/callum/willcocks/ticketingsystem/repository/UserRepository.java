package org.callum.willcocks.ticketingsystem.repository;

import org.callum.willcocks.ticketingsystem.models.Ticket;
import org.callum.willcocks.ticketingsystem.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByDisplayName(String displayName);

    public default Optional<User> getOrCreateUser(String displayName){
        return this.findUserByDisplayName(displayName).or(() -> Optional.of(this.save(new User(displayName))));
    }
}
