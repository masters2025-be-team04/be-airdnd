package rice_monkey.booking.poller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rice_monkey.booking.dao.BookingEventRepository;
import rice_monkey.booking.domain.BookingEvent;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingEventPoller {

    private final BookingEventRepository repo;
    private final KafkaTemplate<String, String> kafka;

    @Scheduled(fixedDelay = 500)
    @Transactional
    public void publish() {
        List<BookingEvent> list = repo.findByPublishedFalse(PageRequest.of(0, 100, Sort.by("id").ascending()));

        list.forEach(ev -> {
            kafka.send("booking.events", ev.getEventType(), ev.getPayload());
            ev.setPublished(true);
        });
    }

}
