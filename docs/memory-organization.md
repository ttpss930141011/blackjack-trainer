# Memory Organization and Context Retention Strategy

Comprehensive memory management system for preventing common development issues.

## Memory File Hierarchy

### Level 1: Project Root (`/CLAUDE.md`)
**Purpose**: Team-shared architectural guidance and anti-pattern prevention
**Scope**: Project-wide rules, DDD/TDD/CQRS patterns, platform-specific guidance
**Audience**: All team members and Claude Code sessions

**Contains**:
- Anti-pattern prevention mechanisms (錯誤, 幻覺, 亂重構, 唬爛, 過度工程)
- Architecture principles and DDD bounded context
- Development commands and quality gates
- Platform abstraction rules

### Level 2: Personal (`/CLAUDE.local.md`)
**Purpose**: Individual developer preferences and session state
**Scope**: Personal coding style, current session context, learning tracking
**Audience**: Individual developer only (not committed to version control)

**Contains**:
- Personal development preferences
- Current session context and TDD cycle state
- Learning notes and open questions
- Personal quality gates and shortcuts

### Level 3: Layer-Specific (`/domain/CLAUDE.md`, `/application/CLAUDE.md`)
**Purpose**: Layer-specific rules and patterns
**Scope**: Architectural layer constraints and implementation guidance
**Audience**: Developers working within specific layers

**Domain Layer**:
- Pure business logic requirements
- Immutability mandates
- Business rule validation patterns

**Application Layer** (future):
- Use case implementation patterns
- Command/query separation rules
- Application service guidelines

**Infrastructure Layer** (future):
- Platform abstraction patterns
- expect/actual implementation rules
- Persistence and UI guidelines

### Level 4: External Documentation (`/docs/*.md`)
**Purpose**: Reference materials and detailed specifications
**Scope**: Comprehensive domain knowledge, patterns, and examples
**Audience**: On-demand reference during development

## Context Retention Mechanisms

### Session State Management

**Session Initialization**:
```markdown
# Session Start Protocol
1. Load hierarchical memory files (automatic)
2. Review CLAUDE.local.md for current context
3. Update "Current Session Context" section
4. Confirm active domain focus and TDD cycle phase
```

**Context Preservation**:
```markdown
# During Development
- Update session context in CLAUDE.local.md as work progresses
- Document new learnings immediately in appropriate sections
- Track assumptions and open questions for validation
- Maintain TDD cycle state awareness
```

**Session Closure**:
```markdown
# Session End Protocol
1. Update CLAUDE.local.md with session outcomes
2. Document new learnings in /docs/ files if significant
3. Update assumptions and open questions
4. Note any architecture decisions made
```

### Knowledge Validation Framework

**Uncertainty Handling**:
```markdown
# When Knowledge is Uncertain
1. Explicitly state uncertainty: "需要驗證 (needs verification)"
2. Document assumption in CLAUDE.local.md
3. Reference appropriate @docs/ file for verification
4. Ask clarifying questions about domain rules
```

**Reference Chain**:
```
Immediate Question → CLAUDE.md (project guidance)
                  → CLAUDE.local.md (personal notes)
                  → @docs/*.md (detailed reference)
                  → External verification (if needed)
```

### Anti-Pattern Prevention Through Memory

**錯誤 (Errors) Prevention**:
- Architecture validation rules in project CLAUDE.md
- Layer-specific constraints in subdirectory CLAUDE.md files
- Quality gates enforcing domain purity and immutability
- TDD cycle state tracking preventing untested code

**幻覺 (Hallucinations) Prevention**:
- Explicit references to verified documentation (@docs/ files)
- Assumption tracking in CLAUDE.local.md
- Knowledge validation requirements in project memory
- "需要驗證" protocol for uncertain information

**亂重構 (Random Refactoring) Prevention**:
- Refactoring safety protocol in quality-gates.md
- TDD cycle state awareness preventing refactoring during red phase
- Architecture compliance checking before structural changes
- Documentation requirements for refactoring rationale

**唬爛 (BS-ing) Prevention**:
- Explicit uncertainty acknowledgment protocols
- Assumption documentation requirements
- Knowledge gap tracking in CLAUDE.local.md
- Question-asking encouragement in memory files

**過度工程 (Over-engineering) Prevention**:
- Progressive complexity framework in architecture.md
- Phase-based feature validation
- Complexity gates preventing premature optimization
- Minimal viable implementation guidance

## Memory Maintenance Strategy

### Regular Updates

**Weekly Review**:
- Update CLAUDE.local.md with new learnings
- Review and validate documented assumptions
- Update @docs/ files with verified information
- Clean up outdated session context

**Monthly Audit**:
- Review project CLAUDE.md for accuracy and completeness
- Update architecture documentation based on decisions made
- Consolidate personal learnings into team knowledge
- Archive or resolve open questions

**Release Cycle**:
- Update documentation to reflect architectural changes
- Validate that memory files align with actual implementation
- Share successful patterns with team
- Document lessons learned for future projects

### Memory Optimization

**Token Efficiency**:
- Keep memory files focused and specific
- Use @docs/ references for detailed information
- Maintain lean project CLAUDE.md for faster loading
- Regularly prune outdated information

**Information Architecture**:
- Organize by frequency of access (frequent in memory, detailed in docs)
- Use clear hierarchies and cross-references
- Maintain consistent terminology across all files
- Provide quick reference sections for common needs

## Integration with Development Workflow

### TDD Cycle Integration

**Red Phase** (Failing Test):
- Update CLAUDE.local.md with current test scenario
- Reference domain rules in @docs/blackjack-rules.md
- Document any business rule questions

**Green Phase** (Passing Test):
- Note successful implementation patterns
- Update domain understanding if needed
- Document any assumptions made during implementation

**Refactor Phase**:
- Follow refactoring safety protocol from quality-gates.md
- Document refactoring rationale
- Validate architecture compliance

### Architecture Decision Integration

**Decision Making**:
- Consult architecture.md for established patterns
- Document new decisions with rationale
- Update appropriate memory files with outcomes
- Share significant decisions with team

**Implementation**:
- Follow layer-specific guidance from subdirectory CLAUDE.md files
- Validate against quality gates
- Update session context with progress

**Validation**:
- Run quality checks from quality-gates.md
- Update documentation with verification results
- Note any deviations from planned architecture

## Troubleshooting Common Issues

### Memory System Issues

**Information Overload**:
- Solution: Move detailed info to @docs/, keep memory files lean
- Validation: Memory files should be quickly scannable

**Outdated Information**:
- Solution: Regular maintenance schedule and validation protocols
- Prevention: Date-stamp decisions and regularly review assumptions

**Inconsistent Information**:
- Solution: Single source of truth principle, cross-reference validation
- Prevention: Use consistent terminology and regular audits

### Development Process Issues

**Context Loss Between Sessions**:
- Solution: Comprehensive CLAUDE.local.md session tracking
- Prevention: Standardized session start/end protocols

**Architecture Drift**:
- Solution: Regular architecture validation and quality gates
- Prevention: Clear constraints in layer-specific memory files

**Knowledge Gaps**:
- Solution: Explicit uncertainty acknowledgment and validation protocols
- Prevention: Assumption tracking and question-asking culture

## Success Metrics

### Quantitative Indicators
- Reduced architecture violations (target: 0 per sprint)
- Improved test coverage stability (target: 100% domain coverage)
- Decreased refactoring-related bugs (track regression rate)
- Faster onboarding time for new team members

### Qualitative Indicators
- Increased confidence in architectural decisions
- Better alignment between team members on domain concepts
- Reduced time spent on debugging platform-specific issues in domain logic
- Improved code review quality and focus

### Memory System Health
- Memory files remain lean and focused
- Documentation stays current with implementation
- Team members actively use and update memory system
- Successful prevention of identified anti-patterns