package models;

public class GroupChat extends Chat {
    public GroupChat(String chatName) {
        super(chatName);
    }

    @Override
    public void displayChatInfo() {
        System.out.print("Group Chat: " + getName() + " with participants: ");
        for (User participant : getParticipants()) {
            System.out.print(participant.getUsername() + " ");
        }
        System.out.println();
    }

    @Override
    public ChatType getType() {
        return ChatType.GROUP;
    }
}