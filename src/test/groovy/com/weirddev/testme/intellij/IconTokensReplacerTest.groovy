package com.weirddev.testme.intellij

import com.weirddev.testme.intellij.icon.IconTokensReplacerImpl
import com.weirddev.testme.intellij.icon.IconizedLabel
import com.weirddev.testme.intellij.icon.Icons

/**
 * Date: 10/15/2016
 * @author Yaron Yamin
 */
class IconTokensReplacerTest extends GroovyTestCase {
    private String text = "TestMe w/ <JUnit4>JUnit4 & <Mockito>Mockito"

    void testStripTokens() {
      assert "TestMe w/ JUnit4 & Mockito" == new IconTokensReplacerImpl().stripTokens(text)
    }

    void testTokenize() {
        assert [new IconizedLabel("TestMe w/ ", Icons.TEST_ME,Icons.TEST_ME), new IconizedLabel("JUnit4 & ", Icons.JUNIT4, Icons.JUNIT4_DARK), new IconizedLabel("Mockito", Icons.MOCKITO, Icons.MOCKITO)] == new IconTokensReplacerImpl().tokenize(text, Icons.TEST_ME)
    }
}
