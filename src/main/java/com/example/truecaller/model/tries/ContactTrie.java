package com.example.truecaller.model.tries;

import java.util.ArrayList;
import java.util.List;

public class ContactTrie {
    private TrieNode root;
    private int indexOfSingleChild;

    public ContactTrie() { this.root = new TrieNode(""); }
    public void insert(String element) {
        TrieNode tempTrieNode = root;
        for(int i = 0;i<element.length(); i++) {
            char c = element.charAt(i);
            int asciiindex = c;
            if(tempTrieNode.getChild(asciiindex) == null) {
                TrieNode trieNode = new TrieNode(String.valueOf(c));
                tempTrieNode.setchild(asciiindex, trieNode);
                tempTrieNode = trieNode;
            } else {
                tempTrieNode = tempTrieNode.getChild(asciiindex);
            }
        }
        tempTrieNode.setLeaf(true);
    }

    public boolean search(String number) {
        TrieNode trienode = root;
        for(int i = 0;i<number.length(); i++) {
            char c = number.charAt(i);
            int asciiIndex = c;
            if(trienode.getChild(asciiIndex) == null) {
                return false;
            } else {
                trienode = trienode.getChild(asciiIndex);
            }
        }
        return true;
    }

    public List<String> allWordsWithPrefix(String prefix) {
        TrieNode trienode = root;
        List<String> allWords = new ArrayList<>();
        for(int i = 0;i<prefix.length();i++) {
            char c = prefix.charAt(i);
            int asciiIndex = c;
            trienode = trienode.getChild(asciiIndex);
        }
        getSuffixes(trienode, prefix, allWords);
        return allWords;
    }

    private void getSuffixes(TrieNode trienode, String prefix, List<String> allWords) {
        if(trienode == null) return;
        if(trienode.isLeaf()) {
            allWords.add(prefix);
        }
        for(TrieNode childTrieNode : trienode.getChildren()) {
            if(childTrieNode == null) continue;
            String childCharacter = childTrieNode.getCharacter();
            getSuffixes(childTrieNode, prefix+childCharacter, allWords);
        }
    }

    public String longestCommonPrefix() {
        TrieNode trieNode = root;
        String longestCharacterPrefix = "";
        while(countNumOfChildren(trieNode) == 1 && !trieNode.isLeaf()) {
            trieNode = trieNode.getChild(indexOfSingleChild);
            longestCharacterPrefix = longestCharacterPrefix + (char) (indexOfSingleChild + 'a');
        }
        return longestCharacterPrefix;
    }

    private int countNumOfChildren(TrieNode trieNode) {
        int numOfChildren = 0;
        for(int i = 0;i<trieNode.getChildren().length; i++) {
            if(trieNode.getChild(i) != null) {
                numOfChildren++;
                indexOfSingleChild = i;
            }
        }
        return numOfChildren;
    }

    public void delete(String key) {
        if((root == null) || (key == null)) {
            System.out.println("Null key or empty trie error");
            return;
        }

        deleteHelper(key, root, key.length(), 0);
        return;
    }

    private boolean deleteHelper(String key, TrieNode currentNode, int length, int level) {
        boolean deletedSelf = false;
        if(currentNode == null) {
            System.out.println("Key does not exist");
            return deletedSelf;
        }
        if(level == length) {
            if(currentNode.isLeaf()) {
                currentNode = null;
                deletedSelf = true;
            } else {
                currentNode.setLeaf(false);
            }
        } else {
            TrieNode childNode = currentNode.getChild(key.charAt(level));
            boolean childDeleted = deleteHelper(key, childNode, length, level+1);
            if(childDeleted) {
                currentNode.setchild(key.charAt(level), null);
                if(currentNode.isLeaf()) {
                    deletedSelf = false;
                } else if (currentNode.getChildren().length > 0) {
                    deletedSelf = false;
                } else {
                    currentNode = null;
                    deletedSelf = true;
                }
            } else {
                deletedSelf = false;
            }
        }

        return deletedSelf;
    }
}
