package api.query;

public enum StatusParticipant {
    WINNER(0),
    PRIZEWINNER(1),
    PARTICIPANT(2);

    private final int value;

    StatusParticipant(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
