package pl.edu.pk.cosmo.habsatbackend.postsservice;

import java.util.List;

public interface EntityFactory<T> {
    public T create();

    public List<T> createMany(int count);
}
