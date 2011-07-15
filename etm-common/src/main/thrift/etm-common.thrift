namespace java com.edmunds.etm.common.thrift

struct HostAddressDto {
    1: string host,
    2: i32 port
}

struct MavenModuleDto {
    1: string groupId,
    2: string artifactId,
    3: string version
}

struct HttpMonitorDto {
    1: string url,
    2: string content
}

struct ClientConfigDto {
    1: HostAddressDto hostAddress,
    2: string contextPath,
    3: MavenModuleDto mavenModule,
    4: list<string> urlRules,
    5: HttpMonitorDto httpMonitor
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
    6: set<ManagementPoolMemberDto> poolMembers
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

