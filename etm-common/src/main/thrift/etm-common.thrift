namespace java com.edmunds.etm.common.thrift

struct HostAddressDto {
    1: string host,
    2: i32 port
}

enum ServiceType {
    MAVEN_WEB_APP = 1,
    RAW_VIP = 2,
    DNS_VIP = 3
}

struct MavenModuleDto {
    1: string groupId,
    2: string artifactId,
    3: string version
}

struct VipConfigDto {
    1: string baseName
    2: i32 port,
    3: bool autoCreate,
    4: bool autoDelete,
}

struct DnsConfigDto {
    1: string fullDomainName
}

struct ServiceConfigDto {
    1: string serviceName,
    2: ServiceType serviceType,
    3: MavenModuleDto mavenModule,
    4: VipConfigDto vipConfig,
    5: DnsConfigDto dnsConfig
}

struct HttpMonitorDto {
    1: string url,
    2: string content
}

struct ServiceProviderDto {
    1: HostAddressDto hostAddress,
    2: ServiceConfigDto serviceConfig
}

struct ClientConfigDto {
    1: HostAddressDto hostAddress,
    2: string contextPath,
    3: MavenModuleDto mavenModule,
    4: list<string> urlRules,
    5: HttpMonitorDto httpMonitor,
    10: list<ServiceProviderDto> serviceProviders
}

struct ManagementPoolMemberDto {
    1: HostAddressDto hostAddress
}

struct ManagementVipDto {
    1: HostAddressDto hostAddress,
    2: string contextPath,
    3: MavenModuleDto mavenModule,
    4: list<string> urlRules,
    5: HttpMonitorDto httpMonitor,
    6: set<ManagementPoolMemberDto> poolMembers,
    10: ServiceConfigDto serviceConfig
}

struct UrlTokenDto {
    1: string name,
    2: string type,
    3: list<string> values
}

struct UrlTokenCollectionDto {
    1: list<UrlTokenDto> tokens
}

struct ControllerInstanceDto {
    1: string id,
    2: string ipAddress,
    3: string version,
    4: string failoverState
}

struct RuleSetDeploymentEventDto {
    1: i64 eventDate,
    2: string ruleSetDigest,
    3: string result
}

struct AgentInstanceDto {
    1: string id,
    2: string ipAddress,
    3: string version,
    4: string activeRuleSetDigest,
    5: RuleSetDeploymentEventDto lastDeploymentEvent,
    6: RuleSetDeploymentEventDto lastFailedDeploymentEvent
}
