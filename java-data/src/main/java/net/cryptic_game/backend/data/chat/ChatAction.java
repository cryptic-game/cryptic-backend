package net.cryptic_game.backend.data.chat;

public enum ChatAction {
    MEMBER_JOIN("member-join"),
    MEMBER_LEAVE("member-leave"),

    SEND_MESSAGE("send-message"),
    WHISPER_MESSAGE("whisper-message"),

    CHANNEL_DELETE("channel-delete");

    final String value;

    ChatAction(final String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
