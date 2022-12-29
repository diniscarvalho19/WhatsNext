package com.dinis.whatsnext;

public class GroupModel {
    private String groupName;
    private String groupMembers;

    public GroupModel(String groupName, String groupMembers) {
        this.groupName = groupName;
        this.groupMembers = groupMembers;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(String groupMembers) {
        this.groupMembers = groupMembers;
    }
}
