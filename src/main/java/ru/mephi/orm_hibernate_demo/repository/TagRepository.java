package ru.mephi.orm_hibernate_demo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.orm_hibernate_demo.entity.Tag;
import ru.mephi.orm_hibernate_demo.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TagRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    public Tag findEntityById(UUID id) {
        Tag tag = entityManager.find(Tag.class, id);
        if (tag == null) {
            throw new NotFoundException("Tag not found with id: " + id);
        }
        return tag;
    }

    public List<Tag> findAll() {
        return entityManager.createQuery("SELECT t FROM Tag t", Tag.class)
                .getResultList();
    }

    public Optional<Tag> findBySlug(String slug) {
        List<Tag> results = entityManager.createQuery(
                "SELECT t FROM Tag t WHERE t.slug = :slug", Tag.class)
                .setParameter("slug", slug)
                .getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Transactional
    public Tag save(Tag tag) {
        if (tag.getId() == null) {
            entityManager.persist(tag);
            return tag;
        } else {
            return entityManager.merge(tag);
        }
    }

    @Transactional
    public void delete(UUID id) {
        Tag tag = findEntityById(id);
        entityManager.remove(tag);
    }

    @Transactional
    public void detachTagRelations(UUID tagId) {
        entityManager.createNativeQuery("DELETE FROM course_tags WHERE tag_id = :tagId")
                .setParameter("tagId", tagId)
                .executeUpdate();
    }
}
