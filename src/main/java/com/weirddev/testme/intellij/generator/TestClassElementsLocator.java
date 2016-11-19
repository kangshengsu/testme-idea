package com.weirddev.testme.intellij.generator;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.Nullable;

public class TestClassElementsLocator {
    public TestClassElementsLocator() {
    }

    public PsiElement findOptimalCursorLocation(PsiClass targetClass) {
        PsiElement defaultLocation = targetClass.getLBrace()==null?targetClass.getLBrace():targetClass.getLBrace().getNextSibling();
        PsiMethod testMethod = findTestMethod(targetClass);
        if (testMethod == null) {
            return defaultLocation;
        }
        PsiElement assertExpression = findLastElement(testMethod, PsiExpressionStatement.class);
        if (assertExpression == null) {
            return defaultLocation;
        } else if (assertExpression.getFirstChild() == null) {
            return defaultLocation;
        } else if (assertExpression.getFirstChild().getLastChild() == null) {
            return defaultLocation;
        } else if (assertExpression.getFirstChild().getLastChild().getFirstChild() == null) {
            return defaultLocation;
        } else if (assertExpression.getFirstChild().getLastChild().getFirstChild().getNextSibling() == null) {
            return defaultLocation;
        } else {
            return assertExpression.getFirstChild().getLastChild().getFirstChild().getNextSibling();
        }
    }

    @Nullable
    PsiMethod findTestMethod(PsiClass targetClass) {
        PsiMethod testMethod = null;
        PsiMethod[] methods = targetClass.getMethods();
        for (PsiMethod method : methods) {
            if (!method.getName().startsWith("setUp")) {
                testMethod = method;
                break;
            }
        }
        return testMethod;
    }

    private <T> T  findLastElement(PsiMethod testMethod,Class<T> elementClass) {
        T foundElement = null;
        if(testMethod.getBody()==null) return null;
        for (PsiElement psiElement : testMethod.getBody().getChildren()) {
            if (elementClass.isInstance(psiElement)) {
                foundElement=elementClass.cast(psiElement);
           }
        }
        return foundElement;
    }
}