package com.acxiom.interview.enums;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 运算符类型
 * @author wangzhiliang
 */
public enum OperatorType {
    ALL(-1, "全部"),

    EQ(0, "等于"),

    LE(1, "小于等于"),
    GE(2, "大于等于"),

    LT(3, "小于"),
    GT(4, "大于"),

    BETWEEN(5, "BETWEEN"),

    IN(6, "IN"),

    LIKE(7, "LIKE"),

    NOT_NULL(8, "非NULL"),

    NOT_EQUAL(9, "不等于"),

    AND(10, "与"),

    OR(11, "或"),

    NOT(12, "非"),   // 暂不支持, 预留
    ;

    private static Logger logger = LoggerFactory.getLogger(OperatorType.class);

    private static final Object _LOCK = new Object();

    private static Map<Integer, OperatorType> _MAP;
    private static List<OperatorType> _LIST;
    private static List<OperatorType> _ALL_LIST;

    private static Set<OperatorType> _ZERO_SET;
    // 单目运算符
    private static Set<OperatorType> _UNARY_SET;
    // 双目运算符
    private static Set<OperatorType> _BINARY_SET;
    // 集合运算符
    private static Set<OperatorType> _COLLECTION_SET;
    // 逻辑运算符
    private static Set<OperatorType> _LOGICAL_SET;

    static {
        synchronized (_LOCK) {
            Map<Integer, OperatorType> map = new HashMap<>();
            List<OperatorType> list = new ArrayList<>();
            List<OperatorType> listAll = new ArrayList<>();
            for (OperatorType type : OperatorType.values()) {
                map.put(type.getValue(), type);
                listAll.add(type);
                if (!type.equals(ALL)) {
                    list.add(type);
                }
            }

            _MAP = ImmutableMap.copyOf(map);
            _LIST = ImmutableList.copyOf(list);
            _ALL_LIST = ImmutableList.copyOf(listAll);

            _ZERO_SET = ImmutableSet.of(NOT_NULL);
            _UNARY_SET = ImmutableSet.of(EQ, LE, GE, LT, GT, LIKE, NOT_EQUAL);
            _BINARY_SET = ImmutableSet.of(BETWEEN);
            _COLLECTION_SET = ImmutableSet.of(IN);

            _LOGICAL_SET = ImmutableSet.of(AND, OR, NOT);
        }
    }

    private int value;
    private String name;

    OperatorType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public static OperatorType get(int value) {
        try {
            return _MAP.get(value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static List<OperatorType> list() {
        return _LIST;
    }

    public static List<OperatorType> listAll() {
        return _ALL_LIST;
    }

    public static boolean isZero(OperatorType operatorType) {
        return _ZERO_SET.contains(operatorType);
    }

    public static boolean isUnary(OperatorType operatorType) {
        return _UNARY_SET.contains(operatorType);
    }

    public static boolean isBinary(OperatorType operatorType) {
        return _BINARY_SET.contains(operatorType);
    }

    public static boolean isCollection(OperatorType operatorType) {
        return _COLLECTION_SET.contains(operatorType);
    }

    public static boolean isLogical(OperatorType operatorType) {
        return _LOGICAL_SET.contains(operatorType);
    }
}
