package com.oneaix.selection.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 2026-06-05 团队协作 JDBC 仓储 */
@SpringBootTest
@Transactional
class JdbcTeamRepositoryTest {

    @Autowired
    private JdbcTeamRepository repository;

    @Test
    void shouldReturnGeneratedIdWhenAddingMember() {
        var member = repository.addMember(
                77L,
                "测试成员",
                "选品买手",
                "editor",
                "tester@example.com",
                "account-tester"
        );

        assertNotNull(member.id());
        assertTrue(member.id() > 0);
    }

    @Test
    void shouldReturnGeneratedIdWhenAddingAssignment() {
        var assignment = repository.addAssignment(
                77L,
                1L,
                "复核供应链报价",
                "测试成员",
                "待确认",
                "测试备注"
        );

        assertNotNull(assignment.id());
        assertTrue(assignment.id() > 0);
    }

    @Test
    void shouldReturnExistingMemberWhenAccountAlreadyExists() {
        var first = repository.addMember(
                78L,
                "重复成员",
                "选品买手",
                "editor",
                "duplicate@example.com",
                "account-duplicate"
        );
        var second = repository.addMember(
                78L,
                "重复成员",
                "选品买手",
                "editor",
                "duplicate@example.com",
                "account-duplicate"
        );

        assertNotNull(first.id());
        assertNotNull(second.id());
        assertTrue(first.id().equals(second.id()));
    }
}
