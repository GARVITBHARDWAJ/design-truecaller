package com.example.truecaller.model.tries;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrieNode {
    public static final int ALPHABET_SIZE = 256;
    private String character;
    @Getter
    private TrieNode[] children;
    private boolean leaf;
    private boolean visited;

    public TrieNode(String character) {
        this.character = character;
        children = new TrieNode[ALPHABET_SIZE];
    }

    public void setchild(int index, TrieNode trieNode) {
        this.children[index] = trieNode;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public TrieNode getChild(int index) {
        return children[index];
    }

}
