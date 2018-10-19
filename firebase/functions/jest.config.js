module.exports = {
    preset: 'ts-jest',
    testEnvironment: 'node',
    rootDir: 'src/',
    clearMocks: true,
    collectCoverage: true,
    coverageDirectory: '../coverage',
    collectCoverageFrom: [
        '**/*.ts',
        '!**/node_modules/**',
        '!**/lib/**',
    ],
    coverageReporters: ["json", "lcov", "text-summary", "clover"],
};