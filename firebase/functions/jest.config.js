module.exports = {
    preset: 'ts-jest',
    testEnvironment: 'node',
    collectCoverage: true,
    coverageDirectory: 'coverage',
    collectCoverageFrom: [
        'src/**/*.ts',
        '!**/node_modules/**',
        '!**/vendor/**',
    ],
    coverageReporters: ["json", "lcov", "text-summary", "clover"],
};