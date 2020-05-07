package api.parse;

public enum StatusParticipant {
    WINNER(0, "Победитель"),
    PRIZEWINNER(1, "Призёр"),
    PARTICIPANT(2, "Участник");

    private final int value;
    private final String stringValue;

    StatusParticipant(int value, String stringValue) {
        this.value = value;
        this.stringValue = stringValue;
    }

    public int getValue() {
        return value;
    }

    public String getStringValue() {
        return stringValue;
    }
}
