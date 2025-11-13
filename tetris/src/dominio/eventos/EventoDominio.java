package dominio.eventos;

import java.time.LocalDateTime;

public abstract class EventoDominio {
    private final LocalDateTime timestamp;

    protected EventoDominio() {
        this.timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() { return timestamp; }
}
