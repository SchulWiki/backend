package com.schulwiki.backend.user.specification;

import com.schulwiki.backend.user.entity.UserEntity;
import com.schulwiki.backend.user.filter.UserFilter;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public class UserSpecification {
    public static Specification<UserEntity> withFilter(UserFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getSearch() != null) {
                String searchPattern = "%" + filter.getSearch().toLowerCase() + "%";
                Predicate searchPredicate = cb.or(
                        cb.like(cb.lower(root.get("firstName")), searchPattern),
                        cb.like(cb.lower(root.get("lastName")), searchPattern),
                        cb.like(cb.lower(root.get("username")), searchPattern),
                        cb.like(cb.lower(root.get("email")), searchPattern)
                );
                predicates.add(searchPredicate);
            }

            if (filter.getRole() != null) {
                predicates.add(cb.like(root.get("role"), "%" + filter.getRole() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
