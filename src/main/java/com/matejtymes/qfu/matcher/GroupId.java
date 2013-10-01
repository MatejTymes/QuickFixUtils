package com.matejtymes.qfu.matcher;

/**
 * @author mtymes
 * @since 10/1/13 2:59 PM
 */
// TODO: add group matcher
class GroupId {

    private final int index;
    private final int groupTag;

    GroupId(int index, int groupTag) {
        this.index = index;
        this.groupTag = groupTag;
    }

    int getIndex() {
        return index;
    }

    int getGroupTag() {
        return groupTag;
    }
}
