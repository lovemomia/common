package cn.momia.common.service;

import java.io.Serializable;

public interface Entity extends Serializable {
    boolean exists();
}
