package com.liang.inject;

public interface UnBinder {
    void unbind();
    UnBinder EMPTY = new UnBinder() {

        @Override
        public void unbind() {
        }
    };
}
