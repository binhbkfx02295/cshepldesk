package com.binhbkfx02295.cshelpdesk.message.repository;

import com.binhbkfx02295.cshelpdesk.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByTicket_Id(int ticketId);
}
