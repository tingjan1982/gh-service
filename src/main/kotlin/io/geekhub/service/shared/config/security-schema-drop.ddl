drop index if exists ix_auth_username;

drop table if exists authorities;

drop table if exists users;

drop table if exists oauth_client_details;

-- Spring Security ACL Schemas
drop table if exists acl_sid cascade;

drop table if exists acl_class cascade;

drop table if exists acl_object_identity cascade;

drop table if exists acl_entry cascade;

