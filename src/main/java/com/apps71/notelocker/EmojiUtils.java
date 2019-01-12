package com.apps71.notelocker;

class EmojiUtils
{
    public static String getSmileyEmoji()
    {
        int unicode = 0x1F60A;
        return getEmojiByUnicode(unicode);
    }

    public static  String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}
