package com.xuyuchen.health.device;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Project {
    private final String id;
    private final String name;
    private final String owner;
    private final Set<String> members = ConcurrentHashMap.newKeySet();
    private volatile boolean enabled = true;
    private final Instant createdAt = Instant.now();
    public Project(String id, String name, String owner) { this.id = id; this.name = name; this.owner = owner; members.add(owner); }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getOwner() { return owner; }
    public Set<String> getMembers() { return Set.copyOf(members); }
    public boolean isEnabled() { return enabled; }
    public Instant getCreatedAt() { return createdAt; }
    public void addMember(String user) { members.add(user); }
    public void removeMember(String user) { if (!owner.equals(user)) members.remove(user); }
    public void disable() { enabled = false; }
}
